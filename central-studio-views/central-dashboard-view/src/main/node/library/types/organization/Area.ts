import type { Unit } from './Unit';
import type { Account } from './Account';

export interface Area {
  id?: string;
  parentId?: string;
  parent?: Area;
  code?: string;
  name?: string;
  type?: string;
  order?: number;
  children?: Area[];
  units?: Unit[];

  creatorId?: string;
  createDate?: number;
  creator?: Account;
  modifierId?: string;
  modifiedDate?: number;
  modifier?: Account;
}
