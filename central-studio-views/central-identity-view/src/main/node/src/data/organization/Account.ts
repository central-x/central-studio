export interface Account {
  id?: string;
  username?: string;
  email?: string;
  mobile?: string;
  name?: string;
  avatar?: string;
  admin?: boolean;
  supervisor?: boolean;

  enabled?: boolean;
  deleted?: boolean;

  creatorId?: string;
  createDate?: number;
  creator?: Account;
  modifierId?: string;
  modifiedDate?: number;
  modifier?: Account;
}