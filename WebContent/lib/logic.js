var userId=0;
function loadCalendar(userId){
		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: ''
			},
			editable: false,
			ignoreTimezone:false,
			timezone: 'local',
			events: {
				url: 'api/users/'+userId+'/receivables',
				startParam: '',
				endParam: '',
				cache: true,
				error: function() {
					$('#script-warning').show();
				}
			},
			loading: function(bool) {
				$('#loading').toggle(bool);
			}
			
		});
		

}
var tableLoaded = false;
function loadTable(userId){
	tableLoaded = true;
    $('#recTable').dataTable( {
	paging: false,
	searching: false,
    	"ajax" : {
	"url": 'api/users/'+userId+'/receivables',
    	    "cache": true,
    	    "dataSrc": ""
    	  },
	"columns":
	[
		{ "data": function ( row, type, val, meta )
			{ 
				return new moment(row.start).format();
			}
			},
		{ "data": "amount" },
		{ "data": function ( row, type, val, meta )
			{ 
				return "<a href='"+row.url+"'>"+row.description+"</a>";
			}

		}
	],
	paging: false
    });
}

function init(userId){
	loadCalendar(userId);
	$('.calendarBtn').click(function() {
		$('.tableDiv').hide();
		$('#calendar').show();
	});
	$('.tableBtn').click(function() {
		if (!tableLoaded){
			loadTable(userId);
		}
		$('#calendar').hide();
		$('.tableDiv').show();
	});

}

