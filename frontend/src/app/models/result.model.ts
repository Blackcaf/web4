/**
 * Модель запроса для проверки попадания точки в область.
 */
export interface PointRequest {
  x: number;
  y: number;
  r: number;
}

/**
 * Модель результата проверки попадания точки.
 * Содержит все данные о проверке, включая время выполнения.
 */
export interface Result {
  id: number;
  x: number;
  y: number;
  r: number;
  hit: boolean;
  timestamp: string;
  executionTime: string;
}

