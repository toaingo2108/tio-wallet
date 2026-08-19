# Biến tên dịch vụ để dễ quản lý
SERVICE_NAME=app

# Lệnh mặc định khi chỉ gõ 'make'
all: deploy

# Lệnh deploy không downtime
deploy:
	docker compose up -d --build --no-deps --force-recreate $(SERVICE_NAME)

deploy-full:
	docker compose up -d --build

# Lệnh xem log thời gian thực
logs:
	docker compose logs -f $(SERVICE_NAME)

# Lệnh dừng và xóa container
stop:
	docker compose stop $(SERVICE_NAME)

# Lệnh dọn dẹp ảnh rác sau khi build
clean:
	docker image prune -f

# Lệnh kiểm tra trạng thái
status:
	docker compose ps
