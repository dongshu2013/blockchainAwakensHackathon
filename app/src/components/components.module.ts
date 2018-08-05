import { NgModule } from '@angular/core';
import { HeaderComponent } from './header/header';
import { ExpandableHeaderComponent } from './expandable-header/expandable-header';
import {IonicModule} from "ionic-angular";
import { CoinCardComponent } from './coin-card/coin-card';
import {ChartModule} from "angular2-highcharts";
import { TransactionCardComponent } from './transaction-card/transaction-card';
import { TokenHolderCardComponent } from './token-holder-card/token-holder-card';
import {CardSwitchComponent} from "./card-switch/card-switch";
import { NewsCardComponent } from './news-card/news-card';
@NgModule({
	declarations: [HeaderComponent,
    ExpandableHeaderComponent,
    CoinCardComponent,
    TransactionCardComponent,
    CardSwitchComponent,
    TokenHolderCardComponent,
    NewsCardComponent],
	imports: [IonicModule,ChartModule],
	exports: [HeaderComponent,
    ExpandableHeaderComponent,
    CoinCardComponent,
    CardSwitchComponent,
    TransactionCardComponent,
    TokenHolderCardComponent,
    NewsCardComponent]
})
export class ComponentsModule {}
