import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { CoinViewPage } from './coin-view';
import {ChartModule} from "angular2-highcharts";
import {HttpClient} from "@angular/common/http";
import {createTranslateLoader} from "../../app/app.module";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {ComponentsModule} from "../../components/components.module";

@NgModule({
  declarations: [
    CoinViewPage,
  ],
  imports: [
    ComponentsModule,
    ChartModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (createTranslateLoader),
        deps: [HttpClient]
      }
    }),
    IonicPageModule.forChild(CoinViewPage),
  ],
})
export class CoinViewPageModule {}
