// Activate jquery-ui sortable element
$(function() {
    $( "#movie-list, #movie-inbox").sortable({
        placeholder: "ui-state-highlight",
        connectWith: ".sortable-connected",
        cursor: "move",
        update: positionUpdated
    }).disableSelection();
});

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

function getMoviePositions() {
    var positions = [];
    $('li.movie-to-sort').each(function(index, element) {
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

function generateDeleteButton(id) {
    var button = '<button class="btn btn-danger btn-xs delete-movie" type="button">'
        + '<span class="glyphicon glyphicon-trash"></sp`an>'
        + '</button>';
    button = $(button).data("movieId", id);
    return button;
}

function archive(item) {
    item.removeClass('movie-to-sort');
    logMoviePositions();
    // add delete button
    var id = item.data("movieId");
    var button = generateDeleteButton(id);
    var container = item.find('div.buttons-container');
    container.append(button);
}

function addToList(item) {
    item.addClass('movie-to-sort');
    logMoviePositions();
    // remove delete button
    item.find('button.delete-movie').remove();
}

function positionUpdated(event, ui) {
    // Several situations are possible, since this function is being called once per
    // sortable container that is involved in the update
    var target = event.target.id;
    if (ui.sender) {
        var sender = ui.sender[0].id;
        var item = (ui.item);
        // it means something is being carried from one list to another.
        if (sender === "movie-list") {
            // list -> inbox = archiving
            archive(item);
        } else {
            // inbox -> list = adding to the order
            addToList(item);
        }
    } else if (target === "movie-list") {
        // either a reorder within the movie list
        // or a carry out of the movie-list
        logMoviePositions();
    }
    // the remaining situation is when things are being reordered in the movie-inbox
    // which we don't care about
}

function logMoviePositions() {
    var positions = getMoviePositions();
    updateMoviePositions(positions);
}

$('span.movie-tag-label').each(function(index, elem) {
    var tag = $(elem);
    var tagColor = tag.data('tagColor');
    tag.css('backgroundColor', tagColor);
});
