import { Component } from '@angular/core';
import { HomePage } from '../home/home';
import {CoinWatchPage} from "../coin-watch/coin-watch";

@Component({
  templateUrl: 'tabs.html'
})
export class TabsPage {

  tab1Root = HomePage;
  tab2Root = 'CoinWatchPage';
  tab3Root = 'SettingsPage';

  constructor() {

  }
}
