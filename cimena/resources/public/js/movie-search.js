$('button.add-movie').click(function() {
    var elem = $(this);
    var movieTitle = elem.data('movieTitle');
    var movieId = elem.data('movieId');
    var movieLink = "https://www.themoviedb.org/movie/" + movieId;
    var params = {
        title: movieTitle,
        link: movieLink
    }
    window.location.href = "/movie/new/populate?" + $.param(params);
});
