import { NgModule } from '@angular/core';
import { HeaderComponent } from './header/header';
import { ExpandableHeaderComponent } from './expandable-header/expandable-header';
import {IonicModule} from "ionic-angular";
import { CoinCardComponent } from './coin-card/coin-card';
import {ChartModule} from "angular2-highcharts";
import { TransactionCardComponent } from './transaction-card/transaction-card';
import { TokenHolderCardComponent } from './token-holder-card/token-holder-card';
@NgModule({
	declarations: [HeaderComponent, ExpandableHeaderComponent,
    CoinCardComponent,
    TransactionCardComponent,
    TokenHolderCardComponent],
	imports: [IonicModule,ChartModule],
	exports: [HeaderComponent, ExpandableHeaderComponent,
    CoinCardComponent,
    TransactionCardComponent,
    TokenHolderCardComponent]
})
export class ComponentsModule {}
