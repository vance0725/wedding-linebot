function rand(max, min) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function unassignUser(username) {
    $.ajax({
        type: "put",
        url: "https://www.vance-susan.life/unAssignUser/" + username,
        success: function (response) {
            console.log("user: " + username + " unassigned!");
        },
        error: function () {
            console.log("unassignUser error! username: " + username);
        },
    });
}