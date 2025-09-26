// src/app/components/informes/informes.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartModule } from 'primeng/chart';
import { TabsModule } from 'primeng/tabs';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-informes',
  standalone: true,
  imports: [
    CommonModule,
    ChartModule,
    TabsModule,
    CardModule
  ],
  template: `
    <p-tabs value="0">
      <p-tablist>
        <p-tab value="0">Balance</p-tab>
        <p-tab value="1">Estadísticas Socios</p-tab>
      </p-tablist>
      <p-tabpanels>
        <p-tabpanel value="0">
          <div class="grid">
            <div class="col-12 lg:col-6">
              <p-card header="Balance Mensual">
                <p-chart type="bar" [data]="balanceData" [options]="chartOptions"></p-chart>
              </p-card>
            </div>
            <div class="col-12 lg:col-6">
              <p-card header="Distribución de Ingresos">
                <p-chart type="pie" [data]="ingresosData" [options]="pieOptions"></p-chart>
              </p-card>
            </div>
          </div>
        </p-tabpanel>

        <p-tabpanel value="1">
          <div class="grid">
            <div class="col-12 lg:col-6">
              <p-card header="Evolución de Socios">
                <p-chart type="line" [data]="sociosData" [options]="chartOptions"></p-chart>
              </p-card>
            </div>
            <div class="col-12 lg:col-6">
              <p-card header="Estado de Cuotas">
                <p-chart type="doughnut" [data]="cuotasData" [options]="pieOptions"></p-chart>
              </p-card>
            </div>
          </div>
        </p-tabpanel>
      </p-tabpanels>
    </p-tabs>
  `
})
export class InformesComponent implements OnInit {
  balanceData: any;
  ingresosData: any;
  sociosData: any;
  cuotasData: any;
  chartOptions: any;
  pieOptions: any;

  ngOnInit() {
    this.inicializarGraficos();
  }

  inicializarGraficos() {
    this.balanceData = {
      labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio'],
      datasets: [
        {
          label: 'Ingresos',
          backgroundColor: '#42A5F5',
          data: [65, 59, 80, 81, 56, 55]
        },
        {
          label: 'Gastos',
          backgroundColor: '#FFA726',
          data: [28, 48, 40, 19, 86, 27]
        }
      ]
    };

    this.ingresosData = {
      labels: ['Cuotas', 'Eventos', 'Donaciones', 'Otros'],
      datasets: [
        {
          data: [300, 50, 100, 75],
          backgroundColor: ['#42A5F5', '#66BB6A', '#FFA726', '#26C6DA']
        }
      ]
    };

    this.chartOptions = {
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    };

    this.pieOptions = {
      plugins: {
        legend: {
          position: 'right'
        }
      }
    };
  }
}
