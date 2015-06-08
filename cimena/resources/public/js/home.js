// initialize bootstrap's tooltips
$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})

function removeMovieWithId(id) {
    $target = $('.movie-id-'+id);
    $target.hide('fast', function() {
        $target.remove();
    });
    return true;
}

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
            removeMovieWithId(movieId);
        }
    });
});

$('span.movie-tag-label').each(function(index, elem) {
    var tag = $(elem);
    var tagColor = tag.data('tagColor');
    tag.css('backgroundColor', tagColor);
});
