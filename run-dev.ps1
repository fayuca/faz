Start-Process powershell -ArgumentList "cd backend; ./mvnw spring-boot:run"
Start-Process powershell -ArgumentList "cd frontend; npm run dev"
