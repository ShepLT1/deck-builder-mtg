terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.17.0"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  profile = "default"
  region = var.aws_region
}

data "aws_availability_zones" "available" {
  state = "available"
}

resource "aws_vpc" "mtg_deck_builder_vpc" {
  cidr_block = var.vpc_cidr_block
  enable_dns_hostnames = true
  enable_dns_support = true
  tags = {
    Name = "mtg_deck_builder_vpc"
  }
}

resource "aws_internet_gateway" "mtg_deck_builder_igw" {
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  tags = {
    Name = "mtg_deck_builder_igw"
  }
}

#resource "aws_eip" "mtg_deck_builder_web_eip" {
#  count = var.subnet_count.public
#  depends_on = [aws_internet_gateway.mtg_deck_builder_igw]
#  tags = {
#    Name = "mtg_deck_builder_web_eip"
#  }
#}

resource "aws_subnet" "mtg_deck_builder_public_subnet" {
  count = var.subnet_count.public
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  cidr_block = var.public_subnet_cidr_blocks[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true
  tags = {
    Name = "mtg_deck_builder_public_subnet_${count.index}"
  }
}

resource "aws_subnet" "mtg_deck_builder_private_subnet" {
  count = var.subnet_count.private
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  cidr_block = var.private_subnet_cidr_blocks[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]
  tags = {
    Name = "mtg_deck_builder_private_subnet_${count.index}"
  }
}

resource "aws_route_table" "mtg_deck_builder_public_rt" {
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  tags = {
    Name = "mtg_deck_builder_public_rt"
  }
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.mtg_deck_builder_igw.id
  }
}

resource "aws_route_table_association" "public" {
  count = var.subnet_count.public
  subnet_id = aws_subnet.mtg_deck_builder_public_subnet[count.index].id
  route_table_id = aws_route_table.mtg_deck_builder_public_rt.id
}

resource "aws_route_table" "mtg_deck_builder_private_rt" {
  count = var.subnet_count.private
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  tags = {
    Name = "mtg_deck_builder_private_rt"
  }
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.mtg_deck_builder_igw.id
  }
}

resource "aws_route_table_association" "private" {
  count = var.subnet_count.private
  subnet_id = aws_subnet.mtg_deck_builder_private_subnet[count.index].id
  route_table_id = aws_route_table.mtg_deck_builder_private_rt[count.index].id
}

resource "aws_ecs_cluster" "mtg_deck_builder_ecs_cluster" {
 name = "mtg-deck-builder-ecs-cluster"
}

data "aws_iam_policy_document" "ecs_node_doc" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_node_role" {
  name_prefix        = "ecs-node-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_node_doc.json
}

resource "aws_iam_role_policy_attachment" "ecs_node_role_policy" {
  role       = aws_iam_role.ecs_node_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_node" {
  name_prefix = "ecs-node-profile"
  path        = "/ecs/instance/"
  role        = aws_iam_role.ecs_node_role.name
}

resource "aws_security_group" "ecs_node_sg" {
  name_prefix = "mtg-deck-builder-ecs-node-sg"
  vpc_id      = aws_vpc.mtg_deck_builder_vpc.id

  egress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_key_pair" "mtg_deck_builder_kp" {
  key_name = "mtg_deck_builder_ec2"
  public_key = file("mtg_deck_builder_ec2.pub")
}

data "aws_ssm_parameter" "ecs_node_ami" {
  name = "/aws/service/ecs/optimized-ami/amazon-linux-2/recommended/image_id"
}

resource "aws_launch_template" "ecs_ec2" {
  name_prefix = "mtg-deck-builder-web"
  image_id = data.aws_ssm_parameter.ecs_node_ami.value
  instance_type = var.settings.web_app.instance_type
  key_name = aws_key_pair.mtg_deck_builder_kp.key_name
  vpc_security_group_ids = [aws_security_group.ecs_node_sg.id, aws_security_group.ec2_sg.id]
  iam_instance_profile {
    arn = aws_iam_instance_profile.ecs_node.arn
  }
  monitoring {
    enabled = true
  }
  user_data = base64encode(<<-EOF
        #!/bin/bash
        echo ECS_CLUSTER=${aws_ecs_cluster.mtg_deck_builder_ecs_cluster.name} >> /etc/ecs/ecs.config;
      EOF
    )
}

resource "aws_autoscaling_group" "ecs_asg" {
  name_prefix = "mtg-deck-builder-ecs-asg"
  vpc_zone_identifier = aws_subnet.mtg_deck_builder_public_subnet[*].id
  desired_capacity = var.settings.web_app.desired_capacity
  max_size = var.settings.web_app.max_size
  min_size = var.settings.web_app.min_size
  health_check_grace_period = var.settings.web_app.health_check_grace_period
  health_check_type = var.settings.web_app.health_check_type
  protect_from_scale_in = var.settings.web_app.protect_from_scale_in
  launch_template {
    id = aws_launch_template.ecs_ec2.id
    version = "$Latest"
  }
  tag {
    key = "AmazonECSManaged"
    value = ""
    propagate_at_launch = true
  }
  tag {
    key = "Name"
    value = "mtg-deck-builder-ecs-cluster"
    propagate_at_launch = true
  }
}

resource "aws_ecs_capacity_provider" "ecs_capacity_provider" {
 name = "mtgdeckbuilder-ecs-capacity-provider"

 auto_scaling_group_provider {
   auto_scaling_group_arn = aws_autoscaling_group.ecs_asg.arn

   managed_scaling {
     maximum_scaling_step_size = 2
     minimum_scaling_step_size = 1
     status                    = "ENABLED"
     target_capacity           = 100
   }
 }
}

resource "aws_ecs_cluster_capacity_providers" "ecs_cluster_capacity_provider" {
 cluster_name = aws_ecs_cluster.mtg_deck_builder_ecs_cluster.name

 capacity_providers = [aws_ecs_capacity_provider.ecs_capacity_provider.name]

 default_capacity_provider_strategy {
   base              = 1
   weight            = 100
   capacity_provider = aws_ecs_capacity_provider.ecs_capacity_provider.name
 }
}

resource "aws_ecr_repository" "mtg_deck_builder_ecr" {
  name                 = "mtg_deck_builder_ecr"
  image_tag_mutability = "MUTABLE"
  force_delete         = true
  image_scanning_configuration {
    scan_on_push = true
  }
}

output "mtg_deck_builder_app_repo_url" {
  value = aws_ecr_repository.mtg_deck_builder_ecr.repository_url
}

data "aws_iam_policy_document" "ecs_task_doc" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_task_role" {
  name_prefix        = "mtg-deck-builder-ecs-task-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_doc.json
}

resource "aws_iam_role" "ecs_exec_role" {
  name_prefix        = "mtg-deck-builder-ecs-exec-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_doc.json
}

resource "aws_iam_role_policy_attachment" "ecs_exec_role_policy" {
  role       = aws_iam_role.ecs_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/mtg-deck-builder"
  retention_in_days = 14
}

resource "aws_ecs_task_definition" "mtg-deck-builder-app" {
  family             = "mtg-deck-builder-app"
  task_role_arn      = aws_iam_role.ecs_task_role.arn
  execution_role_arn = aws_iam_role.ecs_exec_role.arn
  network_mode       = "awsvpc"
  cpu                = 256
  memory             = 256

  container_definitions = jsonencode([{
    name         = "mtg-deck-builder-app",
    image        = "${aws_ecr_repository.mtg_deck_builder_ecr.repository_url}:latest",
    essential    = true,
    portMappings = [{ containerPort = 8080, hostPort = 8080 }],

    environment = [
      { name = "Prod", value = "prod" }
    ]

    logConfiguration = {
      logDriver = "awslogs",
      options = {
        "awslogs-region"        = "us-west-2",
        "awslogs-group"         = aws_cloudwatch_log_group.ecs.name,
        "awslogs-stream-prefix" = "mtg-deck-builder-app"
      }
    },
  }])
}

resource "aws_security_group" "ecs_task" {
  name_prefix = "ecs-task-sg"
  description = "Allow all traffic within the VPC"
  vpc_id      = aws_vpc.mtg_deck_builder_vpc.id

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = [aws_vpc.mtg_deck_builder_vpc.cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_ecs_service" "mtg-deck-builder-app" {
  name            = "mtg-deck-builder-app"
  cluster         = aws_ecs_cluster.mtg_deck_builder_ecs_cluster.id
  task_definition = aws_ecs_task_definition.mtg-deck-builder-app.arn
  desired_count   = 2

  network_configuration {
    security_groups = [aws_security_group.ecs_task.id, aws_security_group.ec2_sg.id]
    subnets         = aws_subnet.mtg_deck_builder_public_subnet[*].id
  }

  capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.ecs_capacity_provider.name
    base              = 1
    weight            = 100
  }

  ordered_placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }

  lifecycle {
    ignore_changes = [desired_count]
  }

  depends_on = [aws_lb_target_group.mtg-deck-builder-app, aws_security_group.ec2_sg]

  load_balancer {
    target_group_arn = aws_lb_target_group.mtg-deck-builder-app.arn
    container_name   = "mtg-deck-builder-app"
    container_port   = 8080
  }

}

resource "aws_security_group" "ec2_sg" {
  name = "mtg_deck_builder_ec2_sg"
  description = "Security group for MTG Deck Builder web servers"
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  ingress {
    description = "Allow SSH"
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    description = "Allow all outbound traffic"
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "alb_sg" {
  name = "mtg_deck_builder_alb_sg"
  description = "Security group for MTG Deck Builder load balancer"
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  ingress {
    description = "Allow HTTPS"
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group_rule" "ingress_ec2_traffic" {
  type                     = "ingress"
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
  security_group_id        = aws_security_group.ec2_sg.id
  source_security_group_id = aws_security_group.alb_sg.id
}

resource "aws_security_group_rule" "egress_alb_traffic" {
  type                     = "egress"
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
  security_group_id        = aws_security_group.alb_sg.id
  source_security_group_id = aws_security_group.ec2_sg.id
}

resource "aws_lb" "main" {
  name               = "mtg-deck-builder-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = aws_subnet.mtg_deck_builder_public_subnet[*].id
  security_groups    = [aws_security_group.alb_sg.id]
}

resource "aws_lb_target_group" "mtg-deck-builder-app" {
  name_prefix = "app-"
  vpc_id      = aws_vpc.mtg_deck_builder_vpc.id
  protocol    = "HTTPS"
  port        = 8080
  target_type = "ip"

  health_check {
    enabled             = true
    path                = "/api/auth/health"
    protocol            = "HTTPS"
    port                = 8080
    matcher             = 200
    interval            = 10
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }
}

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.main.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = "arn:aws:acm:us-west-2:671392292608:certificate/288619a0-c5ed-4056-a145-5500ff2959d6"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.mtg-deck-builder-app.arn
  }
}

output "alb_url" {
  value = aws_lb.main.dns_name
}

resource "aws_route53_zone" "mtg-deck-builder-public-zone" {
  name = var.domain_name
  comment = "${var.domain_name} public zone"
  provider = aws
}

resource "aws_route53_record" "mtg-deck-builder-record" {
  zone_id = aws_route53_zone.mtg-deck-builder-public-zone.zone_id
  name    = "api.${var.domain_name}"
  type    = "A"
  alias {
    name                   = aws_lb.main.dns_name
    zone_id                = aws_lb.main.zone_id
    evaluate_target_health = false
  }
}

#resource "aws_route53_record" "mtg-deck-builder-fe-alias" {
#  zone_id = aws_route53_zone.mtg-deck-builder-public-zone.zone_id
#  name    = var.domain_name
#  type    = "A"
#  alias {
#    name                   = "d3r4g74s1ir5hw.cloudfront.net"
#    zone_id                = "Z2FDTNDATAQYW2"
#    evaluate_target_health = false
#  }
#}
#
#resource "aws_route53_record" "mtg-deck-builder-fe-cname" {
#  zone_id = aws_route53_zone.mtg-deck-builder-public-zone.zone_id
#  name    = var.domain_name
#  type    = "CNAME"
#  ttl     = "300"
#  records = ["d3r4g74s1ir5hw.cloudfront.net"]
#}

resource "aws_security_group" "mtg_deck_builder_db_sg" {
  name = "mtg_deck_builder_db_sg"
  description = "Security group for MTG Deck Builder DB"
  vpc_id = aws_vpc.mtg_deck_builder_vpc.id
  ingress {
    description = "Allow Postgres traffic from only the web sg"
    from_port = 5432
    to_port = 5432
    protocol = "tcp"
    security_groups = [aws_security_group.ec2_sg.id]
  }
  tags = {
    Name = "mtg_deck_builder_db_sg"
  }
}

resource "aws_db_subnet_group" "mtg_deck_builder_db_subnet_group" {
  name = "mtg_deck_builder_db_subnet_group"
  description = "DB subnet group for MTG Deck Builder"
  subnet_ids = [for subnet in aws_subnet.mtg_deck_builder_private_subnet : subnet.id]
}

resource "aws_db_instance" "mtg_deck_builder_db" {
  allocated_storage = var.settings.database.allocated_storage
  engine = var.settings.database.engine
  engine_version = var.settings.database.engine_version
  instance_class = var.settings.database.instance_class
  db_name = var.settings.database.db_name
  username = var.db_username
  password = var.db_password
  port = var.settings.database.port
  db_subnet_group_name = aws_db_subnet_group.mtg_deck_builder_db_subnet_group.id
  vpc_security_group_ids = [aws_security_group.mtg_deck_builder_db_sg.id]
  skip_final_snapshot = var.settings.database.skip_final_snapshot
}