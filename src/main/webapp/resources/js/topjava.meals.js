const mealAjaxUrl = "ui/meals/";

const ctx = {
    ajaxUrl: mealAjaxUrl
};

function updateWithFilter(filterData) {
    $.ajax({
        url: ctx.ajaxUrl + "filter",
        type: "GET",
        data: filterData
    }).done(function (data) {
        updateTableWithData(data);
    });
}

$("#filter").submit(function () {
    let data = $(this).serialize();
    updateWithFilter(data);
    successNoty("Filtered");
    return false;
});

$("#filter button[type='reset']").click(function () {
    $("#filter").get(0).reset();
    updateTable();
});

$(function () {
    makeEditable(
        $("#mealDatatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    );
});