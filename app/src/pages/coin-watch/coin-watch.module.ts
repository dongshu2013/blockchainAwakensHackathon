import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { CoinWatchPage } from './coin-watch';

@NgModule({
  declarations: [
    CoinWatchPage,
  ],
  imports: [
    IonicPageModule.forChild(CoinWatchPage),
  ],
})
export class CoinWatchPageModule {}
