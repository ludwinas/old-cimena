// Activate jquery-ui sortable element
$(function() {
    $( ".sortable" ).sortable({
        placeholder: "ui-state-highlight",
        cursor: "move",
        update: logMoviePositions
    });
    $( ".sortable" ).disableSelection();
});

function deleteMovieWithId(id) {
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
            deleteMovieWithId(movieId);
        }
    });
});

function getMoviePositions() {
    var positions = [];
    $('li.movie-element').each(function(index, element) {
        var movieId = $(element).data('movieId');
        positions.push(movieId);
    });
    return positions;
}

function updateMoviePositions(positions) {
    var csrf = $('#__anti-forgery-token').val();
    $.ajax({
        url: "/update-positions",
        type: "POST",
        data: {
            '__anti-forgery-token': csrf,
            positions: positions
        }
    });
}

function logMoviePositions() {
    var positions = getMoviePositions();
    updateMoviePositions(positions);
}
