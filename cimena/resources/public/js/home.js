$('button.delete-movie').click(function() {
    var element = $(this);
    var movieId = element.data('movieId');
    var csrf = $('#__anti-forgery-token').val();

    var request = $.ajax({
        url: "/movie/delete",
        type: 'POST',
        data: {
            '__anti-forgery-token': csrf,
            'id': movieId
        },
        success: function(result) {
            deleteMovieWithId(movieId);
        }
    });
});

function deleteMovieWithId(id) {
    $target = $('.movie-id-'+id);
    $target.hide('fast', function() {
        $target.remove();
    });
    return true;
}
