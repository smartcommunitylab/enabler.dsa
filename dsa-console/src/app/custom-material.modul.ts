import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http'; import { HttpModule } from '@angular/http';
import {MatButtonModule, MatCheckboxModule, MatSidenavModule, MatToolbarModule, MatIconModule, MatMenuModule, MatTableModule, MatSelectModule, MatDialogModule, MatTooltipModule } from '@angular/material';

@NgModule({
  imports: [MatButtonModule, MatCheckboxModule, MatSidenavModule, MatToolbarModule, MatIconModule, MatMenuModule, MatTableModule, MatSelectModule, MatDialogModule, MatTooltipModule],
  exports: [MatButtonModule, MatCheckboxModule, MatSidenavModule, MatToolbarModule, MatIconModule, MatMenuModule, MatTableModule, MatSelectModule, MatDialogModule, MatTooltipModule],
  declarations: []
})
export class CustomMaterialModule { }
