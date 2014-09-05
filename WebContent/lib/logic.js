var userId=0;
var tableLoaded = false;
var bigLoansLoaded = false;

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
			 eventSources: [
				{
					url: 'api/users/'+userId+'/receivables',
					startParam: '',
					endParam: '',
					cache: true,
					textColor: '#BDFFB6',
					error: function() {$('#script-warning').show();}
				},
				{
					url: 'api/users/'+userId+'/payables',
					startParam: '',
					endParam: '',
					cache: true,
					textColor: '#F5D0A9',
					error: function() {$('#script-warning').show();}
				}
	            ],
			
			
			loading: function(bool) {
				$('#loading').toggle(bool);
			},
			eventRender: function(event, elt, view) {
				var ntoday = new Date().getTime();
				var eventStart = moment( event.start ).valueOf();
			      if (eventStart < ntoday){
			    	  elt.css('background-color','#FA5858');
			      }
			        
			    },
			
		});
		

}

function loadTable(userId){
	tableLoaded = true;
    $('#recTable').dataTable( {
	paging: false,
	searching: false,
    	"ajax" : {
	"url": 'api/users/'+userId+'/calendar',
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
		{ "data": "amount"},
		{ "data": function ( row, type, val, meta )
			{ 
				if (row.amount == ""){
					return '<span style="color:red">'+row.totalPayment+'</span>';
				} else {
					return "";
				}
			}
		},
		{ "data": function ( row, type, val, meta )
			{ 	
				
				return "<a href='"+row.url+"'>"+row.description+"</a>";

			}

		}
	],
	paging: false
    });
}
function loadBigLoans(){
    $('#bigLoansTable').dataTable( {
	paging: false,
	searching: false,
    	"ajax" : {
    		"url": 'api/loans/big',
    	    "cache": true,
    	    "dataSrc": ""
    	  },
	"columns":
	[
		{ "data" : "user"},
		{ "data": function ( row, type, val, meta )
			{ 	
				
				return "<a href='"+row.links[1].href+"' target='_blank'>"+row.title+"</a>";

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
		$('.bigLoansTableDiv').hide();
		$('#calendar').show();
	});
	$('.tableBtn').click(function() {
		if (!tableLoaded){
			loadTable(userId);
		}
		$('#calendar').hide();
		$('.bigLoansTableDiv').hide();
		$('.tableDiv').show();
	});
	$('.bigLoansBtn').click(function() {
		if (!bigLoansLoaded){
			loadBigLoans();
			bigLoansLoaded = true;
		}
		$('#calendar').hide();
		$('.tableDiv').hide();
		$('.bigLoansTableDiv').show();
	});

}

