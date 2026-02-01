import { Routes } from "@angular/router";
import { EventCreate } from "./event-create/event-create";
import { EventDetail } from "./event-detail/event-detail";
import { EventList } from "./event-list/event-list";

export const routes: Routes = [
    {
        path: '',
        component: EventList
    },
    {
        path: 'create',
        component: EventCreate
    }, {
        path: ':eventId',
        component: EventDetail,
    }, {
        path: '**', redirectTo: ''
    }
];