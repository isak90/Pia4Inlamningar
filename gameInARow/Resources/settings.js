//
//
//***************VARIABLES***************

var win = Ti.UI.currentWindow;

Ti.include('appGlobal.js');

var iOS7 = isiOS7Plus();
var theTop = iOS7 ? 20 : 0;

win.top = theTop;

var customFont = 'Chalkboard SE';

var player1Colors = [];
var player2Colors = [];

var picColors = ['images/whitePlayer.png', 'images/bluePlayer.png', 'images/lightGreenPlayer.png', 'images/greenPlayer.png'];
var picColors2 = ['images/blackPlayer.png', 'images/purplePlayer.png', 'images/lightBluePlayer.png', 'images/redPlayer.png'];

buttonPressedSound = Ti.Media.createSound({
	url: 'sounds/marblePlaced.wav',
	volume: 0.5
});



//
//
//***************FUNCTIONS***************

function changeCurrentColor1(currentColor){
	Ti.App.Properties.setString('currentColor1', currentColor);
}

function changeCurrentColor2(currentColor){
	Ti.App.Properties.setString('currentColor2', currentColor);
}

function changeSoundSettings(title){
	if(title == 'On'){
		Ti.App.Properties.setString('sound', true);
	}else{
		Ti.App.Properties.setString('sound', false);
	}
}

function playButtonSound(){
	if(Ti.App.Properties.getString('sound') == 1 || Ti.App.Properties.getString('sound') == null){
		buttonPressedSound.play();
	}else{
		return;
	}
}

function playButtonSoundOnButton(){
	buttonPressedSound.play();
}


//
//
//***************USER INTERFACE***************

var viewNavigation = Ti.UI.createView({
	height: '60dp',
	top: '0dp',
	backgroundImage:'images/settingsNavBar.png'
});
win.add(viewNavigation);

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

var viewOptionPanel = Ti.UI.createView({
	width:'225dp',
	height:'340dp',
	backgroundImage:'images/settingsOptionPanel.png',
	top: '90dp'
});
win.add(viewOptionPanel);

var lblSoundSetting = Ti.UI.createLabel({
	top:'10dp',
	font: {fontFamily: customFont, fontSize: '20sp'},
	text:'Sound:',
	color: '#ffffff'
});
viewOptionPanel.add(lblSoundSetting);

var btnSoundOff = Ti.UI.createButton({
	title: 'Off',
	width:'40dp',
	height:'40dp',
	top:'45dp',
	right:'60dp',
	backgroundImage:'images/settingsOptionButton.png'
});

btnSoundOff.addEventListener('click', function(e){
	btnSoundOn.color = '#ffffff';
	btnSoundOff.color = '#00ff00';
	changeSoundSettings('Off');
});

viewOptionPanel.add(btnSoundOff);

var btnSoundOn = Ti.UI.createButton({
	title: 'On',
	width:'40dp',
	height:'40dp',
	top:'45dp',
	left:'60dp',
	backgroundImage:'images/settingsOptionButton.png',
});

if(Ti.App.Properties.getString('sound') == 1 || Ti.App.Properties.getString('sound') == null){
	btnSoundOn.color = '#00ff00';
	btnSoundOff.color = '#000000';
}else{
	btnSoundOff.color = '#00ff00';
	btnSoundOn.color = '#000000';
}

btnSoundOn.addEventListener('click', function(e){
	btnSoundOff.color = '#ffffff';
	btnSoundOn.color = '#00ff00';
	changeSoundSettings('On');
	playButtonSoundOnButton();
});

viewOptionPanel.add(btnSoundOn);

var viewPlayer1 = Ti.UI.createView({
	top:'110dp',
	width: Ti.UI.SIZE,
	height: '30dp',
	layout: 'horizontal'
});
viewOptionPanel.add(viewPlayer1);

var lblPlayer1Setting = Ti.UI.createLabel({
	font: {fontFamily: customFont, fontSize: '20sp'},
	text:'Player 1:',
	color: '#ffffff'
});
viewPlayer1.add(lblPlayer1Setting);

var viewCurrentColor1 = Ti.UI.createView({
	backgroundImage: Ti.App.Properties.getString('currentColor1'),
	width:'18dp',
	height:'18dp',
	left: '10dp',
	bottom:'5dp',
});
viewPlayer1.add(viewCurrentColor1);

var viewColorPallet1 = Ti.UI.createView({
	layout:'horizontal',
	top:'145dp',
	height:'80dp'
});
viewOptionPanel.add(viewColorPallet1);

for(i = 0 ; i < 4 ; i++){
	player1Colors[i] = Ti.UI.createView({
		backgroundImage: picColors[i],
		width:'40dp',
		height:'40dp',
		left:'13dp'
	});
	
	if(Ti.App.Properties.getString('currentColor1') == null){
		viewCurrentColor1.backgroundImage = 'images/whitePlayer.png';
	}else if(Ti.App.Properties.getString('currentColor1') == player1Colors[i].backgroundImage){
		viewCurrentColor1.backgroundImage = Ti.App.Properties.getString('currentColor1');
	}
	
	player1Colors[i].addEventListener('click', function(e){
		playButtonSound();
		changeCurrentColor1(e.source.backgroundImage);
		viewCurrentColor1.backgroundImage = Ti.App.Properties.getString('currentColor1');
	});
	
	viewColorPallet1.add(player1Colors[i]);
}






var viewPlayer2 = Ti.UI.createView({
	bottom:'90dp',
	width: Ti.UI.SIZE,
	height: '30dp',
	layout: 'horizontal'
});
viewOptionPanel.add(viewPlayer2);

var lblPlayer2Setting = Ti.UI.createLabel({
	text:'Player 2:',
	font: {fontFamily: customFont, fontSize: '20sp'},
	color: '#ffffff'
});
viewPlayer2.add(lblPlayer2Setting);

var viewCurrentColor2 = Ti.UI.createView({
	backgroundImage: Ti.App.Properties.getString('currentColor2'),
	width:'18dp',
	height:'18dp',
	left: '10dp',
	bottom:'5dp',
});
viewPlayer2.add(viewCurrentColor2);

var viewColorPallet2 = Ti.UI.createView({
	layout:'horizontal',
	bottom:'5dp',
	height:'80dp'
});
viewOptionPanel.add(viewColorPallet2);

for(i = 0 ; i < 4 ; i++){
	player2Colors[i] = Ti.UI.createView({
		backgroundImage: picColors2[i],
		width:'40dp',
		height:'40dp',
		left:'13dp'
	});
	
	if(Ti.App.Properties.getString('currentColor2') == null){
		viewCurrentColor2.backgroundImage = 'images/blackPlayer.png';
	}else if(Ti.App.Properties.getString('currentColor2') == player2Colors[i].backgroundImage){
		viewCurrentColor2.backgroundImage = Ti.App.Properties.getString('currentColor2');
	}
	
	player2Colors[i].addEventListener('click', function(e){
		playButtonSound();
		changeCurrentColor2(e.source.backgroundImage);
		viewCurrentColor2.backgroundImage = Ti.App.Properties.getString('currentColor2');
	});
	
	viewColorPallet2.add(player2Colors[i]);
}




