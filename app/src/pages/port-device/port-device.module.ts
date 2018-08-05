import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { PortDevicePage } from './port-device';

@NgModule({
  declarations: [
    PortDevicePage,
  ],
  imports: [
    IonicPageModule.forChild(PortDevicePage),
  ],
})
export class PortDevicePageModule {}
