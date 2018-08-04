import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs/BehaviorSubject";

/*
  Generated class for the ThemeProvider provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class ThemeProvider {

  private theme: BehaviorSubject<String>;

  constructor() {
    let selectedTheme = 'dark-theme';
    if(window.localStorage.getItem('THEME')) {
      selectedTheme = window.localStorage.getItem('THEME');
    }
    this.theme = new BehaviorSubject(selectedTheme);
  }

  setActiveTheme(val) {
    this.theme.next(val);
  }

  getActiveTheme() {
    return this.theme.asObservable();
  }

}
