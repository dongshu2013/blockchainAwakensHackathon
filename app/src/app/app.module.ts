import { NgModule, ErrorHandler } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { MyApp } from './app.component';

import { HomePage } from '../pages/home/home';
import { TabsPage } from '../pages/tabs/tabs';

import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import {ComponentsModule} from "../components/components.module";
import { ThemeProvider } from '../providers/theme/theme';
import {SecureStorage} from "@ionic-native/secure-storage";
import {Clipboard} from "@ionic-native/clipboard";
import {QRScanner} from "@ionic-native/qr-scanner";
import { RestProvider } from '../providers/rest/rest';
import {NotSupported} from "../pages/not-supported/not-supported";
import {Push} from "@ionic-native/push";
import {Badge} from "@ionic-native/badge";
import {AppVersion} from "@ionic-native/app-version";
import {AppRate} from "@ionic-native/app-rate";
import {SocialSharing} from "@ionic-native/social-sharing";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {IonicStorageModule} from "@ionic/storage";
import {Device} from "@ionic-native/device";
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { ChartModule } from 'angular2-highcharts';
import {HighchartsStatic} from "angular2-highcharts/dist/HighchartsService";
import {PopoverPage} from "../pages/popover/popover";
import {CoinViewPageModule} from "../pages/coin-view/coin-view.module";
import {NgxQRCodeModule} from "ngx-qrcode2";
import {NativePageTransitions} from "@ionic-native/native-page-transitions";



declare const require: any;


export function highchartsFactory() {
  const hc = require('highcharts');
  return hc;
}

export function createTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    NotSupported,
    PopoverPage,
    TabsPage
  ],
  imports: [
    BrowserModule,
    ComponentsModule,
    HttpClientModule,
    CoinViewPageModule,
    NgxQRCodeModule,
    ChartModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (createTranslateLoader),
        deps: [HttpClient]
      }
    }),
    IonicStorageModule.forRoot(),

    IonicModule.forRoot(MyApp,{
      swipeBackEnabled: true,
      tabsHideOnSubPages: false,
      ios: {
        statusbarPadding: true
      }
    }),
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage,
    NotSupported,
    PopoverPage,
    TabsPage
  ],
  providers: [
    Push,
    Badge,
    AppVersion,
    AppRate,
    Device,
    StatusBar,
    SplashScreen,
    SecureStorage,
    SocialSharing,
    Clipboard,
    QRScanner,
    RestProvider,
    HttpClient,
    NativePageTransitions,
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    ThemeProvider,
    {
      provide: HighchartsStatic,
      useFactory: highchartsFactory
    }
  ]
})
export class AppModule {}
