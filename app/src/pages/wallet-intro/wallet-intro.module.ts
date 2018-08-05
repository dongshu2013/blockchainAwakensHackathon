import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { WalletIntroPage } from './wallet-intro';

@NgModule({
  declarations: [
    WalletIntroPage,
  ],
  imports: [
    IonicPageModule.forChild(WalletIntroPage),
  ],
})
export class WalletIntroPageModule {}
