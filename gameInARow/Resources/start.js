var win = Ti.UI.currentWindow;

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


var viewOptionWrapper = Ti.UI.createView({
	height:'405dp',
	backgroundColor:'transparent'
});
win.add(viewOptionWrapper);

var viewTitle = Ti.UI.createView({
	backgroundImage: 'images/startTitle.png',
	top:'0dp',
	height:'63dp',
	width:'255dp'
});
viewOptionWrapper.add(viewTitle);

var viewOptionPanel = Ti.UI.createView({
	backgroundImage: 'images/optionPanel.png',
	top:'90dp',
	height:'275dp',
	width:'225dp'
});
viewOptionWrapper.add(viewOptionPanel);

var btnNewGame = Ti.UI.createButton({
	title: 'New Game',
	backgroundImage: 'images/optionPanelButton.png',
	top:'15dp',
	height:'50dp',
	width:'200dp',
	font: {fontFamily: customFont, fontSize: '18sp'},
});

btnNewGame.addEventListener('click', function(e){
	
	playButtonSound();
	
	if(Ti.Platform.displayCaps.platformHeight == 568){
		var gameWin = Ti.UI.createWindow({
			url: 'game.js',
			backgroundImage: 'images/backgroundLarge.png'
		});	
	}else{
		var gameWin = Ti.UI.createWindow({
			url: 'game4.js',
			backgroundImage: 'images/backgroundLarge.png'
		});	
	}
	gameWin.open({
  		modal:true,
  		modalTransitionStyle: Ti.UI.iPhone.MODAL_TRANSITION_STYLE_FLIP_HORIZONTAL,
	});
			
});

viewOptionPanel.add(btnNewGame);

var btnSettings = Ti.UI.createButton({
	title: 'Settings',
	backgroundImage: 'images/optionPanelButton.png',
	top:'80dp',
	height:'50dp',
	width:'200dp',
	font: {fontFamily: customFont, fontSize: '18sp'},
});

btnSettings.addEventListener('click', function(e){
	
	playButtonSound();
	
	var settingsWin = Ti.UI.createWindow({
		url: 'settings.js',
		backgroundImage: 'images/backgroundLarge.png',
	});
	
	settingsWin.addEventListener('close', function(e){
		
	});
	
	settingsWin.open({
  		modal:true,
  		modalTransitionStyle: Ti.UI.iPhone.MODAL_TRANSITION_STYLE_FLIP_HORIZONTAL,
	});
});

viewOptionPanel.add(btnSettings);

var btnRules = Ti.UI.createButton({
	title: 'Rules',
	backgroundImage: 'images/optionPanelButton.png',
	top:'145dp',
	height:'50dp',
	width:'200dp',
	font: {fontFamily: customFont, fontSize: '18sp'},
});

btnRules.addEventListener('click', function(e){
	
	playButtonSound();
	
	var aboutWin = Ti.UI.createWindow({
		url: 'rules.js',
		backgroundImage: 'images/backgroundLarge.png'
	});
	aboutWin.open({
  		modal:true,
  		modalTransitionStyle: Ti.UI.iPhone.MODAL_TRANSITION_STYLE_FLIP_HORIZONTAL,
	});		
});

viewOptionPanel.add(btnRules);

var btnAbout = Ti.UI.createButton({
	title: 'About Pentago',
	backgroundImage: 'images/optionPanelButton.png',
	top:'210dp',
	height:'50dp',
	width:'200dp',
	font: {fontFamily: customFont, fontSize: '18sp'},
});

btnAbout.addEventListener('click', function(e){
	
	playButtonSound();
	
	var aboutWin = Ti.UI.createWindow({
		url: 'aboutPentago.js',
		backgroundImage: 'images/backgroundLarge.png'
	});
	aboutWin.open({
  		modal:true,
  		modalTransitionStyle: Ti.UI.iPhone.MODAL_TRANSITION_STYLE_FLIP_HORIZONTAL,
	});		
});

viewOptionPanel.add(btnAbout);

