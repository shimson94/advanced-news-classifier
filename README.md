# Advanced News Classifier

A machine learning system for automated news article classification using deep neural networks and GloVe word embeddings.

## Overview

This project implements a sophisticated text classification pipeline that categorizes news articles into distinct groups. The system leverages Stanford's GloVe (Global Vectors for Word Representation) embeddings combined with a custom neural network architecture to achieve high-accuracy classification.

## Technical Architecture

### Core Components

- **Text Preprocessing Pipeline**: Stanford CoreNLP integration for tokenization, POS tagging, and lemmatization
- **Embedding Layer**: GloVe 50-dimensional word vectors for semantic representation
- **Neural Network**: Multi-layer perceptron with configurable hidden layers
- **Classification Engine**: Softmax output layer for multi-class categorization

### Key Features

- **Intelligent Text Processing**: Automatic stopword filtering and text normalization
- **Dynamic Embedding Sizing**: Median-based document length calculation for optimal performance
- **Robust Training Pipeline**: Separate training and testing data workflows
- **Performance Optimization**: Efficient memory management for large-scale processing

## How It Works

1. **Document Ingestion**: HTML news articles are parsed and content extracted
2. **Text Preprocessing**: Content undergoes cleaning, tokenization, and lemmatization
3. **Feature Extraction**: Words are converted to GloVe embeddings and aggregated into document vectors
4. **Neural Classification**: Deep learning model processes embeddings to predict article categories
5. **Result Generation**: Classified articles are grouped and formatted for output

## Performance Metrics

- **GloVe Loading**: < 280ms target for 38,000+ vocabulary terms
- **Document Processing**: < 30ms average per article
- **Classification Accuracy**: > 90% on test dataset
- **Memory Efficiency**: Optimized INDArray operations for large embedding matrices

## Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Minimum 4GB RAM (recommended 8GB for optimal performance)

### Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/AdvancedNewsClassifier.git
cd AdvancedNewsClassifier
```

2. Install dependencies:
```bash
mvn clean compile
```

3. Run the classifier:
```bash
mvn exec:java -Dexec.mainClass="AdvancedNewsClassifier"
```

## Testing

Execute the comprehensive test suite:
```bash
mvn test
```

The test suite includes:
- Performance benchmarks for critical operations
- Accuracy validation for classification results
- Memory usage optimization tests
- Edge case handling verification

## Technology Stack

- **Framework**: Deeplearning4j (DL4J) for neural network implementation
- **NLP**: Stanford CoreNLP for advanced text processing
- **Embeddings**: Pre-trained GloVe vectors from Stanford NLP Group
- **Build Tool**: Apache Maven
- **Testing**: JUnit 5 with performance assertions

## Project Structure

```
src/
├── main/java/
│   ├── AdvancedNewsClassifier.java    # Main classification engine
│   ├── ArticlesEmbedding.java         # Document embedding processor
│   ├── Toolkit.java                   # Utility and I/O operations
│   └── ...                           # Supporting classes
├── main/resources/
│   ├── News/                         # Sample news articles
│   └── glove.6B.50d_Reduced.csv     # GloVe embeddings
└── test/java/                        # Comprehensive test suite
```

## Configuration

The system supports various configuration options:

- **Batch Size**: Configurable training batch size (default: 10)
- **Embedding Dimensions**: 50-dimensional GloVe vectors
- **Network Architecture**: Customizable hidden layer sizes
- **Learning Parameters**: Adjustable learning rate and optimization settings

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with appropriate tests
4. Submit a pull request

## Future Enhancements

- **Multi-language Support**: Extend classification to non-English articles
- **Real-time Processing**: Stream processing capabilities for live news feeds
- **Advanced Architectures**: Integration with transformer-based models
- **API Development**: RESTful API for external system integration

## References

- Pennington, J., Socher, R., & Manning, C. (2014). GloVe: Global Vectors for Word Representation
- Stanford CoreNLP Natural Language Processing Toolkit
- Eclipse Deeplearning4j: Deep Learning for Java