import type { Area } from '@/api/data/organization/Area';
import type { Department } from '@/api/data/organization/Department';
import type { Account } from '@/api/data/organization/Account';

export interface Unit {
  id?: string;
  parentId?: string;
  parent?: Unit;
  code?: string;
  name?: string;
  areaId?: string;
  area?: Area;
  order?: number;
  children?: Unit[];
  departments?: Department[];

  creatorId?: string;
  createDate?: number;
  creator?: Account;
  modifierId?: string;
  modifiedDate?: number;
  modifier?: Account;
}
