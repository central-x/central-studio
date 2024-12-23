import type { Unit } from '@/api/data/organization/Unit';
import type { Account } from '@/api/data/organization/Account';

export interface Department {
  id?: string;
  parentId?: string;
  parent?: Department;
  unitId?: string;
  unit?: Unit;
  code?: string;
  name?: string;
  type?: string;
  children?: Department[];

  creatorId?: string;
  createDate?: number;
  creator?: Account;
  modifierId?: string;
  modifiedDate?: number;
  modifier?: Account;
}
