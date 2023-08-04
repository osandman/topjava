const mealAjaxUrl = "profile/meals/";

const ctx = {
    ajaxUrl: mealAjaxUrl,
    updateTable: updateWithFilter
}

function updateWithFilter() {
    $.ajax({
        url: mealAjaxUrl + "filter",
        type: "GET",
        data: $("#filter").serialize()
    }).done(updateTableWithData);
}

$("#filter").submit(function () {
    updateWithFilter();
    successNoty("Filtered");
    return false;
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