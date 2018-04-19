import { BaseEntity } from './../../shared';

export class Message implements BaseEntity {
    constructor(
        public id?: number,
        public code?: string,
        public messageBody?: string,
        public dateInsert?: any,
    ) {
    }
}
