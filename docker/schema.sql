CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    data_cadastro DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE usuario_interesses (
    usuario_id BIGINT NOT NULL,
    interesse VARCHAR(40) NOT NULL,
    PRIMARY KEY (usuario_id, interesse),
    CONSTRAINT fk_interesse_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
