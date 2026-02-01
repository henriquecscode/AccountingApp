import { Routes } from "@angular/router";
import { EventCreate } from "./event-create/event-create";

export const routes: Routes = [
    {
        path: 'create',
        component: EventCreate
    }
];