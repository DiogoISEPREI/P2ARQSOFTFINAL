package pt.psoft.g1.psoftg1.readermanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.message.ReaderCreatedMessage;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {
    private final ReaderRepository readerRepo;
    private final UserRepository userRepo;
    private final ReaderMapper readerMapper;
    private final GenreRepository genreRepo;
    private final ForbiddenNameRepository forbiddenNameRepository;
    private final PhotoRepository photoRepository;
    private final ReaderMessagePublisher readerMessagePublisher;


    @Override
    public ReaderDetails create(CreateReaderRequest request, String photoURI) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists!");
        }

        Iterable<String> words = List.of(request.getFullName().split("\\s+"));
        for (String word : words){
            if(!forbiddenNameRepository.findByForbiddenNameIsContained(word).isEmpty()) {
                throw new IllegalArgumentException("Name contains a forbidden word");
            }
        }

        List<String> stringInterestList = request.getInterestList();
        List<Genre> interestList = this.getGenreListFromStringList(stringInterestList);

        /*if(stringInterestList != null && !stringInterestList.isEmpty()) {
            request.setInterestList(this.getGenreListFromStringList(stringInterestList));
        }*/

        /*
         * Since photos can be null (no photo uploaded) that means the URI can be null as well.
         * To avoid the client sending false data, photoURI has to be set to any value / null
         * according to the MultipartFile photo object
         *
         * That means:
         * - photo = null && photoURI = null -> photo is removed
         * - photo = null && photoURI = validString -> ignored
         * - photo = validFile && photoURI = null -> ignored
         * - photo = validFile && photoURI = validString -> photo is set
         * */

        MultipartFile photo = request.getPhoto();
        if(photo == null && photoURI != null || photo != null && photoURI == null) {
            request.setPhoto(null);
        }

        int count = readerRepo.getCountFromCurrentYear();
        Reader reader = readerMapper.createReader(request);
        ReaderDetails rd = readerMapper.createReaderDetails(count+1, reader, request, photoURI, interestList);

        readerMessagePublisher.publishReaderCreated(
        new ReaderCreatedMessage(
                request.getUsername(),
                request.getFullName(),
                request.getBirthDate(),
                request.getPhoneNumber(),
                true, // gdprConsent (já validado antes)
                request.getMarketing(),
                request.getThirdParty(),
                photoURI,
                request.getInterestList(),
                UUID.randomUUID()
            )
        );


        userRepo.save(reader);
        return readerRepo.save(rd);
    }

    

    @Override
    public ReaderDetails update(final Long id, final UpdateReaderRequest request, final long desiredVersion, String photoURI){
        final ReaderDetails readerDetails = readerRepo.findByUserId(id)
                .orElseThrow(() -> new NotFoundException("Cannot find reader"));

        List<String> stringInterestList = request.getInterestList();
        List<Genre> interestList = this.getGenreListFromStringList(stringInterestList);

         /*
         * Since photos can be null (no photo uploaded) that means the URI can be null as well.
         * To avoid the client sending false data, photoURI has to be set to any value / null
         * according to the MultipartFile photo object
         *
         * That means:
         * - photo = null && photoURI = null -> photo is removed
         * - photo = null && photoURI = validString -> ignored
         * - photo = validFile && photoURI = null -> ignored
         * - photo = validFile && photoURI = validString -> photo is set
         * */

        MultipartFile photo = request.getPhoto();
        if(photo == null && photoURI != null || photo != null && photoURI == null) {
            request.setPhoto(null);
        }

        readerDetails.applyPatch(desiredVersion, request, photoURI, interestList);

        userRepo.save(readerDetails.getReader());
        return readerRepo.save(readerDetails);
    }

    
    @Override
    public Optional<ReaderDetails> removeReaderPhoto(String readerNumber, long desiredVersion) {
        ReaderDetails readerDetails = readerRepo.findByReaderNumber(readerNumber)
                .orElseThrow(() -> new NotFoundException("Cannot find reader"));

        String photoFile = readerDetails.getPhoto().getPhotoFile();
        readerDetails.removePhoto(desiredVersion);
        Optional<ReaderDetails> updatedReader = Optional.of(readerRepo.save(readerDetails));
        photoRepository.deleteByPhotoFile(photoFile);
        return updatedReader;
    }

    private List<Genre> getGenreListFromStringList(List<String> interestList) {
        if(interestList == null) {
            return null;
        }

        if(interestList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Genre> genreList = new ArrayList<>();
        for(String interest : interestList) {
            Optional<Genre> optGenre = genreRepo.findByString(interest);
            if(optGenre.isEmpty()) {
                throw new NotFoundException("Could not find genre with name " + interest);
            }

            genreList.add(optGenre.get());
        }

        return genreList;
    }



    @Override
    public Optional<ReaderDetails> findByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUsername'");
    }

    public void createFromEvent(
            String username,
            String fullName,
            String birthDate,
            String phoneNumber,
            boolean gdpr,
            boolean marketing,
            boolean thirdParty,
            String photoUri,
            List<String> interestList
    ) {

        if (userRepo.findByUsername(username).isPresent()) {
            return; // idempotência básica
        }

        Reader reader = Reader.newReader(
                username,
                "event-generated-password",
                fullName
        );

        userRepo.save(reader);

        List<Genre> genres = getGenreListFromStringList(interestList);

        int count = readerRepo.getCountFromCurrentYear();

        ReaderDetails readerDetails = new ReaderDetails(
                count + 1,
                reader,
                birthDate,
                phoneNumber,
                gdpr,
                marketing,
                thirdParty,
                photoUri,
                genres
        );

        readerRepo.save(readerDetails);
    }

}
