import { Component, Input } from '@angular/core';

@Component({
	selector: 'app-factor-de-riesgo',
	templateUrl: './factor-de-riesgo.component.html',
	styleUrls: ['./factor-de-riesgo.component.scss']
})
export class FactorDeRiesgoComponent{

	@Input() description: string;
	@Input() riskFactor: RiskFactor;

	constructor() {
	}


}

export interface RiskFactor {
	value?: number;
	effectiveTime?: Date;
}
