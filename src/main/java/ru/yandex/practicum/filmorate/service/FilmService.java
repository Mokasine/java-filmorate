package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.InMemoryDataStore;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final InMemoryDataStore store;
    private final UserService userService;

    public FilmService(InMemoryDataStore store, UserService userService) {
        this.store = store;
        this.userService = userService;
    }

    public List<Film> getAll() {
        return List.copyOf(store.films.values());
    }

    public Film getById(Integer id) {
        Film film = store.films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        normalizeFilm(film);
        return film;
    }

    public Film add(Film film) {
        validate(film);
        normalizeFilm(film);
        film.setId(store.nextFilmId());
        store.films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("ID фильма должен быть указан");
        }
        Film existing = getById(film.getId());
        validate(film);
        normalizeFilm(film);
        if (film.getLikes() == null) {
            film.setLikes(existing.getLikes());
        }
        store.films.put(film.getId(), film);
        return film;
    }

    public void delete(Integer id) {
        getById(id);
        store.films.remove(id);
        store.reviews.values().removeIf(r -> Objects.equals(r.getFilmId(), id));
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = getById(filmId);
        userService.getById(userId);
        film.getLikes().add(userId);
        userService.addEvent(userId, filmId, "LIKE", "ADD");
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = getById(filmId);
        userService.getById(userId);
        film.getLikes().remove(userId);
        userService.addEvent(userId, filmId, "LIKE", "REMOVE");
    }

    public List<Film> getPopular(Integer count, Integer genreId, Integer year) {
        int limit = count == null ? 10 : count;
        return store.films.values().stream()
                .filter(f -> genreId == null || f.getGenres().stream().anyMatch(g -> Objects.equals(g.getId(), genreId)))
                .filter(f -> year == null || f.getReleaseDate().getYear() == year)
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparing(Film::getId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Film> search(String query, String by) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String q = query.toLowerCase();
        Set<String> modes = Arrays.stream(Optional.ofNullable(by).orElse("title").split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return store.films.values().stream()
                .filter(f -> {
                    boolean title = modes.contains("title") && f.getName() != null && f.getName().toLowerCase().contains(q);
                    boolean director = modes.contains("director") && f.getDirectors().stream()
                            .anyMatch(d -> d.getName() != null && d.getName().toLowerCase().contains(q));
                    return title || director;
                })
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparing(Film::getId))
                .collect(Collectors.toList());
    }

    public List<Film> getByDirector(Integer directorId, String sortBy) {
        Director director = store.directors.get(directorId);
        if (director == null) {
            throw new NotFoundException("Режиссер с id " + directorId + " не найден");
        }
        Comparator<Film> comparator = "likes".equalsIgnoreCase(sortBy)
                ? Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparing(Film::getId)
                : Comparator.comparing(Film::getReleaseDate).thenComparing(Film::getId);
        return store.films.values().stream()
                .filter(f -> f.getDirectors().stream().anyMatch(d -> Objects.equals(d.getId(), directorId)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public Genre getGenre(Integer id) {
        Genre genre = store.genres.get(id);
        if (genre == null) {
            throw new NotFoundException("Жанр с id " + id + " не найден");
        }
        return genre;
    }

    public List<Genre> getGenres() {
        return List.copyOf(store.genres.values());
    }

    public Mpa getMpa(Integer id) {
        Mpa mpa = store.mpas.get(id);
        if (mpa == null) {
            throw new NotFoundException("Рейтинг MPA с id " + id + " не найден");
        }
        return mpa;
    }

    public List<Mpa> getMpas() {
        return List.copyOf(store.mpas.values());
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            film.setMpa(getMpa(film.getMpa().getId()));
        } else {
            film.setMpa(getMpa(1));
        }
    }

    private void normalizeFilm(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<>());
        } else {
            Set<Genre> normalizedGenres = film.getGenres().stream()
                    .filter(g -> g != null && g.getId() != null)
                    .map(g -> getGenre(g.getId()))
                    .collect(Collectors.toCollection(TreeSet::new));
            film.setGenres(normalizedGenres);
        }

        if (film.getDirectors() == null) {
            film.setDirectors(new TreeSet<>());
        } else {
            Set<Director> normalizedDirectors = film.getDirectors().stream()
                    .filter(d -> d != null && d.getId() != null)
                    .map(d -> {
                        Director director = store.directors.get(d.getId());
                        if (director == null) {
                            throw new NotFoundException("Режиссер с id " + d.getId() + " не найден");
                        }
                        return director;
                    })
                    .collect(Collectors.toCollection(TreeSet::new));
            film.setDirectors(normalizedDirectors);
        }

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getMpa() == null) {
            film.setMpa(getMpa(1));
        }
    }
}
