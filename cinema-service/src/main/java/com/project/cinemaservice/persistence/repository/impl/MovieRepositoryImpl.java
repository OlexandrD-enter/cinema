package com.project.cinemaservice.persistence.repository.impl;


import com.project.cinemaservice.domain.dto.movie.MovieFilters;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetails;
import com.project.cinemaservice.persistence.enums.MovieFileType;
import com.project.cinemaservice.persistence.model.Genre;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.QMovie;
import com.project.cinemaservice.persistence.model.QMovieFile;
import com.project.cinemaservice.persistence.model.QMovieGenre;
import com.project.cinemaservice.persistence.repository.MovieRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

/**
 * Custom implementation of MovieRepository for advanced querying.
 */
@Repository
public class MovieRepositoryImpl extends QuerydslRepositorySupport implements
    MovieRepositoryCustom {

  private static final QMovie qMovie = QMovie.movie;
  private static final QMovieFile qMovieFile = QMovieFile.movieFile;
  private static final QMovieGenre qMovieGenre = QMovieGenre.movieGenre;
  private final JPAQueryFactory queryFactory;

  public MovieRepositoryImpl(EntityManager entityManager) {
    super(Movie.class);
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<MoviePageDetails> findAllByFilters(Pageable pageable,
      MovieFilters movieFilters) {

    JPAQuery<Tuple> query = queryFactory
        .select(
            qMovie.id,
            qMovie.name,
            qMovie.ageLimit,
            qMovie.language,
            qMovie.country,
            qMovie.realiseDate,
            qMovieFile.fileId
        )
        .from(qMovie)
        .leftJoin(qMovie.movieGenres)
        .leftJoin(qMovie.movieFiles, qMovieFile)
        .where(
            qMovieFile.movieFileType.eq(MovieFileType.MOVIE_PREVIEW)
                .and(getPredicateBasedOnFilters(movieFilters))
        );

    query.groupBy(qMovie.id, qMovieFile.fileId);

    applySorting(query, pageable.getSort());

    long total = query.stream().count();

    List<MoviePageDetails> moviePageDetails = query
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch()
        .stream()
        .map(tuple -> {
          List<String> genreNamesList = queryFactory
              .select(qMovieGenre.genre.name)
              .from(qMovieGenre)
              .where(qMovieGenre.movie.id.eq(tuple.get(qMovie.id)))
              .fetch();
          return new MoviePageDetails(
              tuple.get(qMovie.id),
              tuple.get(qMovie.name),
              tuple.get(qMovie.ageLimit),
              tuple.get(qMovie.language),
              tuple.get(qMovie.country),
              tuple.get(qMovie.realiseDate),
              tuple.get(qMovieFile.fileId),
              genreNamesList
          );
        })
        .collect(Collectors.toList());

    return new PageImpl<>(moviePageDetails, pageable, total);
  }

  /**
   * Constructs a Predicate based on provided movie filters.
   *
   * @param movieFilters The filters to be applied.
   * @return             A Predicate representing the filter criteria.
   */
  public Predicate getPredicateBasedOnFilters(MovieFilters movieFilters) {
    BooleanBuilder predicateBuilder = new BooleanBuilder();

    if (Objects.nonNull(movieFilters.getGenres()) && !movieFilters.getGenres().isEmpty()) {
      predicateBuilder.and(qMovie.id.in(getMovieIdsByGenres(movieFilters.getGenres())));
    }

    if (Objects.nonNull(movieFilters.getLanguage())) {
      predicateBuilder.and(qMovie.language.eq(movieFilters.getLanguage()));
    }

    if (Objects.nonNull(movieFilters.getCountry())) {
      predicateBuilder.and(qMovie.country.eq(movieFilters.getCountry()));
    }

    if (Objects.nonNull(movieFilters.getMinAge()) && Objects.nonNull(movieFilters.getMaxAge())) {
      predicateBuilder.and(
          qMovie.ageLimit.between(movieFilters.getMinAge(), movieFilters.getMaxAge()));
    }

    return predicateBuilder;
  }

  private void applySorting(JPAQuery<Tuple> query, Sort sort) {
    OrderSpecifier<?> orderSpecifier = sort.isSorted()
        ? createOrderSpecifier(sort.iterator().next())
        : new OrderSpecifier<>(Order.DESC, qMovie.id);
    query.orderBy(orderSpecifier);
  }

  private OrderSpecifier<?> createOrderSpecifier(
      Sort.Order order) {

    Optional<Expression<?>> sortAttribute = getSortAttribute(order.getProperty());

    return sortAttribute.map(expression -> order.getDirection() == Sort.Direction.ASC
            ? new OrderSpecifier(Order.ASC, expression)
            : new OrderSpecifier(Order.DESC, expression))
        .orElse(new OrderSpecifier(Order.DESC, qMovie.id));
  }

  private Optional<Expression<?>> getSortAttribute(String productSortAttribute) {
    return switch (productSortAttribute.toLowerCase()) {
      case "name" -> Optional.of(qMovie.name);
      case "id" -> Optional.of(qMovie.id);
      default -> Optional.empty();
    };
  }

  private List<Long> getMovieIdsByGenres(List<Genre> genres) {
    JPAQuery<Long> query = queryFactory.select(qMovieGenre.movie.id)
        .from(qMovieGenre)
        .groupBy(qMovieGenre.movie.id)
        .having(qMovieGenre.movie.count().eq((long) genres.size()))
        .where(qMovieGenre.genre.in(genres));

    return query.fetch();
  }
}
