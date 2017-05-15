$(document).ready(function () {
  $('#get-data').click(function () {
    var showData = $('#show-data');

    $.getJSON('http://localhost:8080/reddit-manager/subreddit/count', function (data) {
      console.dir(data);

      var items = data.map(function (item) {
        return item._id + ': ' + item.count;
      });

      showData.empty();

      if (items.length) {
        var content = '<li>' + items.join('</li><li>') + '</li>';
        var list = $('<ul />').html(content);
        showData.append(list);
      }
    });

    showData.text('Loading the JSON file.');
  });
});
