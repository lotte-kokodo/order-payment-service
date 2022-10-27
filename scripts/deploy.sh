export DOCKER_ID="nykwon"
export SERVICE_NAME="order"

# 가동중인 컨테이너 중단 및 삭제
sudo docker ps -a -q --filter "name=$SERVICE_NAME" | grep -q . && docker stop $SERVICE_NAME && docker rm $SERVICE_NAME | true

# 기존 이미지 삭제
sudo docker rmi $DOCKER_ID/$SERVICE_NAME

# 도커허브 이미지 Pull
# TAG 생략 시 :latest (가장 최신에 Push 된 이미지)
sudo docker pull $DOCKER_ID/$SERVICE_NAME

# 도커 Run
docker run -d -p 80:8080 -e TZ=Asia/Seoul -v /home/ec2-user/logs:/logs --name $SERVICE_NAME $DOCKER_ID/$SERVICE_NAME

# 사용하지 않는 불필요한 이미지 삭제 (현재 컨테이너가 물고 있는 이미지는 삭제 X)
docker rmi -f $(docker images -f "dangling=true" -q) || true