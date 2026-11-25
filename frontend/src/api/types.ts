export interface TokenPair {
  accessToken: string;
  refreshToken: string;
}

export interface CurrentUser {
  userId: number;
  roles: string[];
  email: string;
}

export interface Session extends TokenPair {
  user: CurrentUser;
}

export interface LoginInput {
  email: string;
  password: string;
}

export interface RegisterInput extends LoginInput {
  firstName: string;
  lastName: string;
  age: number;
}

export interface Product {
  productId: number;
  userId: number;
  userProductId: number | null;
  name: string;
  price: number;
  count: number;
  countOfSales: number;
  averageEstimation: number;
}

export interface ProductInput {
  name: string;
  price: number;
  count: number;
  countOfSales: number;
  averageEstimation: number;
}

export interface SellerStats {
  userId: number;
  totalProducts: number;
  totalSales: number;
  totalStockValue: number;
  averageEstimation: number;
  updatedAt: string;
}
