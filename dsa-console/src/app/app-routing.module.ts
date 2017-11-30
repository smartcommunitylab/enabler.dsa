import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HeaderComponent }      from './components/header/header.component';
import { BodyComponent }      from './components/body/body.component';
import { DatasetsComponent }      from './components/datasets/datasets.component';
import { ManagersComponent }      from './components/managers/managers.component';
import { UsersComponent }      from './components/users/users.component';

const routes: Routes = [
  { path: 'header', component: HeaderComponent },
  { path: 'body', component: BodyComponent },
  { path: 'datasets', component: DatasetsComponent },
  { path: 'managers', component: ManagersComponent },
  { path: 'users', component: UsersComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
