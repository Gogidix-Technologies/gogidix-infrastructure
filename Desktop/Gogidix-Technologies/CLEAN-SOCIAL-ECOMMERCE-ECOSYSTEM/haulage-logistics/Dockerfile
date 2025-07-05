# Multi-stage build for Haulage Logistics Domain
# This Dockerfile supports both Java services and Node.js services

# Stage 1: Java services build
FROM openjdk:17-jdk-alpine AS java-builder
WORKDIR /app
COPY pom.xml .
COPY java-services/ ./java-services/
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

# Stage 2: Node.js services build  
FROM node:18-alpine AS node-builder
WORKDIR /app
COPY node-services/package*.json ./
RUN npm ci --only=production && npm cache clean --force
COPY node-services/ .
RUN npm run build

# Stage 3: Production runtime for Java services
FROM openjdk:17-jre-alpine AS java-runtime
WORKDIR /app
RUN addgroup --system --gid 1001 haulage
RUN adduser --system --uid 1001 haulage
COPY --from=java-builder --chown=haulage:haulage /app/java-services/target/*.jar ./app.jar
USER haulage
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
CMD ["java", "-jar", "app.jar"]

# Stage 4: Production runtime for Node.js services
FROM node:18-alpine AS node-runtime
WORKDIR /app
ENV NODE_ENV production
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nodejs
COPY --from=node-builder --chown=nodejs:nodejs /app/dist ./dist
COPY --from=node-builder --chown=nodejs:nodejs /app/node_modules ./node_modules
USER nodejs
EXPOSE 3000
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3000/health || exit 1
CMD ["node", "dist/index.js"]

# Stage 5: Production runtime for frontend apps
FROM nginx:alpine AS frontend-runtime
WORKDIR /app
RUN addgroup --system --gid 1001 frontend
RUN adduser --system --uid 1001 frontend --ingroup frontend
COPY --from=node-builder --chown=frontend:frontend /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
USER frontend
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/health || exit 1
CMD ["nginx", "-g", "daemon off;"]