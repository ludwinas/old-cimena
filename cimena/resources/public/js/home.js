$('button.delete-movie').click(function() {
    var element = $(this);
    var movieId = element.data('movieId');
    var csrf = $('#__anti-forgery-token').val();
    var deleteUrl = "/movie/"+encodeURIComponent(movieId)+"/delete";

    var request = $.ajax({
        url: deleteUrl,
        type: 'POST',
        data: {
            '__anti-forgery-token': csrf,
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

$(function() {
    $( ".sortable" ).sortable({
        placeholder: "ui-state-highlight",
        cursor: "move"
    });
    $( ".sortable" ).disableSelection();
});
