var win = Ti.UI.currentWindow;

Ti.include('appGlobal.js');

var iOS7 = isiOS7Plus();
var theTop = iOS7 ? 20 : 0;

win.top = theTop;

var customFont = "Chalkboard SE";

buttonPressedSound = Ti.Media.createSound({
	url: 'sounds/marblePlaced.wav',
	volume: 0.5
});


function playButtonSound(){
	if(Ti.App.Properties.getString('sound') == 1 || Ti.App.Properties.getString('sound') == null){
		buttonPressedSound.play();
	}else{
		return;
	}
}

var viewNavigation = Ti.UI.createView({
	height: '60dp',
	top: '0dp',
	backgroundImage:'images/navBarClean.png'
});
win.add(viewNavigation);

var viewNavBarTitle = Ti.UI.createView({
	height: '30dp',
	width:'155dp',
	backgroundImage:'images/navBarTitleRules.png'
});
viewNavigation.add(viewNavBarTitle);

var btnBack = Ti.UI.createButton({
	title: 'Back',
	left: '10dp',
	backgroundImage: 'images/navBarButton.png',
	width:'60dp',
	height:'40dp'
});

btnBack.addEventListener('click', function(e){
	playButtonSound();
	win.close();		
});

viewNavigation.add(btnBack);

var textAreaRules = Ti.UI.createTextArea({
	top:'65dp',
	left:'10dp',
	right:'10dp',
	height: Ti.Platform.displayCaps.platformHeight - 85,
	scrollable:true,
	value: "The game starts with an empty game board. The game board consists of the four separate boards. The starting player places a marble in a socket of his or her choice. After placing a marble the player turns any one of the four boards one notch (90 degrees) clock- or counter clock- wise. A board, not necessarily the one on which the marble has been placed, must be turned each move.\n\nThen the second player does the same, i.e. places a marble and turns a board. So on and so forth.\n\nThe first player to get five marbles in a row wins! The row can be horizontal, vertical or diagonal and run over two or three boards. If a player gets five in a row when placing a marble he or she does not need to turn a board. If all the sockets have been filled without any player getting five in a row the game is a draw. If both players get five in a row as a player turns a board the game is also a draw.\n",
	font: {fontFamily: customFont, fontSize: '16sp'},
	color:'#ffffff',
	backgroundColor:'transparent',
	editable:false,
});

win.add(textAreaRules);

