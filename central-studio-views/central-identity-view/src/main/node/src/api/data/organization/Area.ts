import type { Unit } from '@/api/data/organization/Unit';
import type { Account } from '@/api/data/organization/Account';

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
