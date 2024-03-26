import type { Account, Unit } from '@/api/data/organization/Organization'

export interface Role {
  id?: string;
  applicationId?: string;
  code?: string;
  name?: string;
  unitId?: string;
  unit?: Unit;
  enabled?: boolean;
  remark?: string;

  creatorId?: string;
  createDate?: number;
  creator?: Account;
  modifierId?: string;
  modifiedDate?: number;
  modifier?: Account;
}