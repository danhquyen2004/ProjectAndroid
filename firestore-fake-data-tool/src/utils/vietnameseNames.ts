const firstNames = [
  'Nguyễn', 'Trần', 'Lê', 'Phạm', 'Hoàng', 'Phan', 'Vũ', 'Đặng', 'Bùi', 'Đỗ', 'Hồ', 'Ngô', 'Dương', 'Đinh'
];

const middleNames = [
  'Thị', 'Văn', 'Hữu', 'Minh', 'Gia', 'Tuấn', 'Thanh', 'Quốc', 'Phúc', 'Khánh', 'Anh'
];

const lastNames = [
  'An', 'Bình', 'Châu', 'Dũng', 'Đức', 'Hà', 'Hải', 'Hạnh', 'Hiếu', 'Huy', 'Khoa', 'Linh', 'Loan', 'Long',
  'Mai', 'Nam', 'Ngọc', 'Phong', 'Quang', 'Sơn', 'Thảo', 'Thành', 'Trang', 'Trung', 'Tuấn', 'Tú', 'Vân', 'Việt'
];

export function getRandomVietnameseName(): string {
  const first = firstNames[Math.floor(Math.random() * firstNames.length)];
  const middle = middleNames[Math.floor(Math.random() * middleNames.length)];
  const last = lastNames[Math.floor(Math.random() * lastNames.length)];
  return `${first} ${middle} ${last}`;
}
