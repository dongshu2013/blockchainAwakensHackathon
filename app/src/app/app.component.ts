import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';

import { TabsPage } from '../pages/tabs/tabs';
import {ThemeProvider} from "../providers/theme/theme";
import {RestProvider} from "../providers/rest/rest";

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage:any = TabsPage;
  selectedTheme: String;

  constructor(public platform: Platform,
              public statusBar: StatusBar,
              public authService: RestProvider,
              public themeProvider: ThemeProvider,
              public splashScreen: SplashScreen) {
    this.themeProvider.getActiveTheme().subscribe(val => this.selectedTheme = val);
    this.authService.setTheme(this.selectedTheme);

    this.webOnly();
    //this.phoneOnly();
  }

  phoneOnly(){
    this.platform.ready().then(() => {
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    });
  }

  webOnly (){

  }
}
