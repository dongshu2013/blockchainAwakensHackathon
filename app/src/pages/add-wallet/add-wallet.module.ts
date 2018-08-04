import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { AddWalletPage } from './add-wallet';
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {createTranslateLoader} from "../../app/app.module";
import {HttpClient} from "@angular/common/http";

@NgModule({
  declarations: [
    AddWalletPage,
  ],
  imports: [
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (createTranslateLoader),
        deps: [HttpClient]
      }
    }),
    IonicPageModule.forChild(AddWalletPage),
  ],
})
export class AddWalletPageModule {}
