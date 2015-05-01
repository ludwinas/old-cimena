$('button.add-movie').click(function() {
    var elem = $(this);
    var movieTitle = elem.data('movieTitle');
    var movieOriginalTitle = elem.data('movieOriginalTitle');
    var movieId = elem.data('movieId');
    var movieLink = "https://www.themoviedb.org/movie/" + movieId;
    if (movieOriginalTitle === movieTitle) {
        movieOriginalTitle = "";
    }
    var params = {
        title: movieTitle,
        original_title: movieOriginalTitle,
        link: movieLink
    }
    window.location.href = "/movie/new/populate?" + $.param(params);
});
