package pt.psoft.g1.psoftg1.genremanagement.api;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-04T15:21:04+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class GenreViewMapperImpl extends GenreViewMapper {

    @Override
    public GenreView toGenreView(Genre genre) {
        if ( genre == null ) {
            return null;
        }

        GenreView genreView = new GenreView();

        genreView.setGenre( map( genre.getGenre() ) );

        return genreView;
    }

    @Override
    public GenreView mapStringToGenreView(String genre) {
        if ( genre == null ) {
            return null;
        }

        GenreView genreView = new GenreView();

        genreView.setGenre( map( genre ) );

        return genreView;
    }
}
