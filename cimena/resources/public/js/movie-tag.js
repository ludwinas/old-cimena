function removeMovieTagWithId(id) {
    $target = $('.movie-tag-id-'+id);
    $target.hide('fast', function() {
        $target.remove();
    });
    return true;
}

$('button.delete-movie-tag').click(function() {
    var element = $(this);
    var movieTagId = element.data('movieTagId');
    var csrf = $('#__anti-forgery-token').val();
    var deleteUrl = "/movie-tag/"+encodeURIComponent(movieTagId)+"/delete";

    var request = $.ajax({
        url: deleteUrl,
        type: 'POST',
        data: {
            '__anti-forgery-token': csrf,
        },
        success: function(result) {
            removeMovieTagWithId(movieTagId);
        }
    });
});

$('span.movie-tag-label').each(function(index, elem) {
    var tag = $(elem);
    var tagColor = tag.data('tagColor');
    tag.css('backgroundColor', tagColor);
});
