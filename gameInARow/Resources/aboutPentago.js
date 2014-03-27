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
	backgroundImage:'images/navBarTitleAbout.png'
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

var textAreaAbout = Ti.UI.createTextArea({
	top:'65dp',
	left:'10dp',
	right:'10dp',
	height: Ti.Platform.displayCaps.platformHeight - 85,
	scrollable:true,
	value: "Pentago is a fast and fun new board game, as easy as it is sophisticated. The first player to get five in a row wins, but winning might be harder than you think. Every time you place a marble you also turn one of the game’s four boards. The ever-changing game board makes the game all the more challenging.\n\nThe game was invented by Tomas Flodén from Sweden and due to the strategic nature of the game it has been widely used among Mensa members and was voted a Mensa selection game in 2006.\n\nMay the smartest person win!\n",
	font: {fontFamily: customFont, fontSize: '16sp'},
	color:'#ffffff',
	backgroundColor:'transparent',
	editable:false
});
win.add(textAreaAbout);

// Comments for the entire class...
//
//
