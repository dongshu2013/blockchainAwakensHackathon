import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { GenerateQrCodePage } from './generate-qr-code';
import {NgxQRCodeModule} from "ngx-qrcode2";

@NgModule({
  declarations: [
    GenerateQrCodePage,
  ],
  imports: [
    NgxQRCodeModule,
    IonicPageModule.forChild(GenerateQrCodePage),
  ],
})
export class GenerateQrCodePageModule {}
