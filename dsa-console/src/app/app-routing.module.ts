import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HeaderComponent }      from './components/header/header.component';
import { BodyComponent }      from './components/body/body.component';

const routes: Routes = [
  { path: 'header', component: HeaderComponent },
  { path: 'body', component: BodyComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
